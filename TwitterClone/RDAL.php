<?php
require_once '../predis/autoload.php';

function getRandomAuthentication() {
    // I think it's safe to assume we'll not have
    // more than a million users in the near future
    $data = strval(rand(0, 10000));
    // Returns the hexadecmal representation of a string
    return md5($data);
}

function isLoggedIn() {
    // These are our global variables for accessing user info
    global $User, $_COOKIE;

    if (isset($User)) return true;

    if (isset($_COOKIE['auth'])) {
        $r = connection();
        $authcookie = $_COOKIE['auth'];
        // if $authcookie differs from uid:$userid:auth, i.e. the user exists but is logged out, 
        // we return false so she/he will have to log in again
        if ($userid = $r->get("auth:$authcookie")) {
            if ($r->get("uid:$userid:auth") != $authcookie) return false;
            loadUserInfo($userid);
            return true;
        }
    }
    return false;
}

function loadUserInfo($userid) {
    global $User;

    $r = connection();
    $User['id'] = $userid;
    $User['username'] = $r->get("uid:$userid:username");
    return true;
}

function connection() {
    static $r = false;

    if ($r) return $r;

    // Predis only requires that we provide the host address, in this
    // case localhost, and the default port on which it runs is 6379
    $link = array(
        'host'      => '127.0.0.1',
        'port'      => 6379
    );

    // We simply provide the localhost IP and port number, and 
    // Predis handles the rest
    $r = new Predis\Client($link);
    return $r;
}

function prioritizedGet($param) {
    global $_GET, $_POST, $_COOKIE;

    // Check cookie first, since the  only important data in there
    // will be the userid connected with a particular authentication string
    if (isset($_COOKIE[$param])) return $_COOKIE[$param];
    if (isset($_POST[$param])) return $_POST[$param];
    if (isset($_GET[$param])) return $_GET[$param];
    return false;
}

// Usable wrapper for prioritizedGet
function simpleGet($param) {
    $val = prioritizedGet($param);
    if ($val === false) return false;
    return trim($val);
}

function utf8entities($s) {
    return htmlentities($s,ENT_COMPAT,'UTF-8');
}

// This javascript:history.back() thing is cool
function goBack($msg) {
    include("header.php");
    echo('<div id ="error">'.utf8entities($msg).'<br>');
    echo('<a href="javascript:history.back()">Please try again</a></div>');
    include("footer.php");
    exit;
}

function strElapsed($t) {
    $d = time()-$t;
    // if we're talking about less than 60 seconds, express
    // in terms of seconds
    if ($d < 60) return "$d seconds";
    // if we're talking about less than 3600 (the number of seconds in an hour)
    // express in terms of minutes
    if ($d < 3600) {
        $m = (int)($d/60);
        return "$m minute".($m > 1 ? "s" : "");
    }
    // if we're talking about less than a day, express in terms of hours
    if ($d < 3600*24) {
        $h = (int)($d/3600);
        return "$h hour".($h > 1 ? "s" : "");
    }
    // We're talking days here
    $d = (int)($d/(3600*24));
    return "$d day".($d > 1 ? "s" : "");
}

function showPost($pid) {
    global $User;

    $r = connection();
    $postdata = $r->get("post:$pid");
    if (!$postdata) return false;

    // tokenize the post
    $useraccountid = $User['id'];
    $aux = explode("|",$postdata);
    $id = $aux[0];
    $time = $aux[1];
    $username = $r->get("uid:$id:username");
    $post = join(array_splice($aux,2,count($aux)-2),"|");
    // put the time associated with the post into human readable form
    $elapsed = strElapsed($time);
    $userlink = "<a class=\"username\" href=\"userdetail.php?u=".urlencode($username)."\">".utf8entities($username)."</a>";

    echo("<form method=\"POST\" action=\"removepost.php?postid=$pid&userid=$id\">");
    echo('<div class="post">'.$userlink.' '.utf8entities($post));
    // Only present remove button if it's the users *own* post
    if (intval($id) == intval($useraccountid)) {
        echo("<input type=\"submit\" id=\"btnRmv\" value=\"Remove Post\">");
    }
    echo("<br>");
    echo('<i>posted '.$elapsed.' ago via web</i></div>');
    echo("</form>");
    return true;
}

function showAllPosts($userid, $start, $count, $includeFollowees)
{
    $r = connection();
    // If userid is -1, return all posts; otherwise return all posts
    // made by particular user
    $key = ($userid == -1) ? "global:timeline" : "uid:$userid:posts";
    // If includeFollowees is true, get all posts of people the user follows
    if ($includeFollowees)
    {
        // We pass the same $count parameter since we know that AT MOST 
        // we will have to display $count posts - no point in retrieving 
        // more than that
        $posts = getUserAndFolloweePosts($userid, $start, $count);
    }
    else 
    {
        $posts = $r->lrange($key, $start, $start+$count);
    }
    $c = 0;
    // Stop showing posts when we've reached count posts
    foreach($posts as $p)
    {
        if (showPost($p)) $c++;
        if ($c == $count) break;
    }
    return count($posts) == $count+1;
}

function showFollowerPosts($userid, $start, $count)
{
    $r = connection();
    $followers = $r->smembers("uid:$userid:followers");
    $posts;
    // add *all* follower posts to timeline
    foreach($followers as $f)
    {
        $posts = addPostsToTimeline($f, $posts, $start, $count);
    }
    // only display the latest $count posts
    $c = 0;
    foreach ($posts as $p)
    {
        if (showPost($p)) $c++;
        if ($c == $count) break;
    }
}

function showFolloweePosts($userid, $start, $count)
{
    $r = connection();
    $followees = $r->smembers("uid:$userid:following");
    $posts;
    foreach($followees as $f)
    {
        $posts = addPostsToTimeline($f, $posts, $start, $count);
    }
    $c = 0;
    foreach ($posts as $p)
    {
        if (showPost($p)) $c++;
        if ($c == $count) break;
    }
}

function getUserAndFolloweePosts($userid, $start, $count)
{
    $r = connection();
    // Get all users this user is following
    $followees = $r->smembers("uid:$userid:following");
    // Get $count posts for this user
    $posts = $r->lrange("uid:$userid:posts", $start, $start+$count);
    foreach ($followees as $f)
    {
        $posts = addPostsToTimeline($f, $posts, $start, $count);
    }
    return $posts;
}

function addPostsToTimeline($userid, $timeline, $start, $count)
{
    $r = connection();
    $posts = $r->lrange("uid:$userid:posts", $start, $start+$count);
    foreach($posts as $p)
    {
        // PHP magic
        $timeline[] = $p;
    }
    rsort($timeline, SORT_NUMERIC);
    return($timeline);
}

function showUserPostsWithPagination($username,$userid,$start,$count, $includeFollowees) {
    global $_SERVER;
    $thispage = $_SERVER['PHP_SELF'];

    $navlink = "";
    $next = $start+10;
    $prev = $start-10;
    $nextlink = $prevlink = false;
    if ($prev < 0) $prev = 0;

    $u = $username ? "&u=".urlencode($username) : "";
    if (showAllPosts($userid,$start,$count, $includeFollowees))
        $nextlink = "<a href=\"$thispage?start=$next".$u."\">Older posts &raquo;</a>";
    if ($start > 0) {
        $prevlink = "<a href=\"$thispage?start=$prev".$u."\">&laquo; Newer posts</a>".($nextlink ? " | " : "");
    }
    if ($nextlink || $prevlink)
        echo("<div class=\"rightlink\">$prevlink $nextlink</div>");
}

function showRecentRegistered() {
    $r = connection();
    // Sort all users in the database by uid, i.e. by register date/time
    $users = $r->sort("global:users","GET uid:*:username DESC LIMIT 0 10");
    echo("<div>");
    $recentUserCount = 0;
    foreach($users as $u) {
        $recentUserCount++;
        $name = $r->get("uid:$u:username");
        echo("<a class=\"username\" href=\"userdetail.php?u=".urlencode($name)."\">".utf8entities($name)."</a>");
        if ($recentUserCount % 6 == 0)
        {
            echo ("<br />");
        }
    }
    echo("</div><br>");
}

function showFollowers($userid)
{
    $r = connection();
    $users = $r->sort("uid:$userid:followers", "GET uid:*:username DESC LIMIT 0 10");
    echo("<div>");
    $followerCount = 0;
    foreach($users as $u)
    {
        $followerCount++;
        $name = $r->get("uid:$u:username");
        echo("<a class=\"username\" href=\"userdetail.php?u=".urlencode($name)."\">".utf8entities($name)."</a>");
        if ($followerCount % 6 == 0)
        {
            echo("<br />");
        }
    }
    echo("</div><br />");
}

function showFollowees($userid)
{
    $r = connection();
    $users = $r->sort("uid:$userid:following", "GET uid:*:username DESC LIMIT 0 10");
    echo("<div>");
    $followeeCount = 0;
    foreach($users as $u)
    {
        $followeeCount++;
        $name = $r->get("uid:$u:username");
        echo("<a class=\"username\" href=\"userdetail.php?u=".urlencode($name)."\">".utf8entities($name)."</a>");
        if ($followeeCount % 6 == 0)
        {
            echo("<br />");
        }
    }
    echo("</div><br />");
}

?>
