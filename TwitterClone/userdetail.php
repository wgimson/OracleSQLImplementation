<?php
    include("RDAL.php");
    include("header.php");

    $r = connection();
    // Ensure that we both got the username from the query string, and also 
    // that this username is connected with a userid
    if (!simpleGet("u") || !($userid = $r->get("username:".simpleGet("u").":id"))) 
    {
        echo("<h2 class=\"username\">We cannot find a user by that name.</h2>");
        exit(1);
    }

    echo("<h2 class=\"username\">".simpleGet("u")."</h2>");
    // If the user is logged in and their id is different than the user they clicked on
    if (isLoggedIn() && $User['id'] != $userid)
    {
        $isFollowing = $r->sismember("uid:".$User['id'].":following", $userid);
        if (!$isFollowing)
        {
            echo("<a href=\"followuser.php?uid=$userid&f=1\" class=\"button\">Follow this user</a>");
        }
        else 
        {
            echo("<a href=\"followuser.php?uid=$userid&f=0\" class=\"button\">Stop following</a>");
        }
    }
    else 
    {
        echo("<p><em>No point following yourself, that would just be weird.</em></p>");
    }
?>
<?php
    $start = simpleGet("start") === false ? 0 : intval(simpleGet("start"));
    showUserPostsWithPagination(simpleGet("u"), $userid, $start, 10, false);
    include("footer.php");
?>
