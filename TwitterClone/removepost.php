<?php
    include("RDAL.php");
    $postid = simpleGet("postid");
    $userid = simpleGet("userid");
    $r = connection();
    // remove post
    $r->del("post:$postid");
    // remove from users list of posts
    $r->lrem("uid:$userid:posts", 0, $postid);
    $followers = $r->smembers("uid:$userid:followers");
    foreach ($followers as $f)
    {
        // remove for each follower
        $r->lrem("uid:$f:posts", 0, $postid);
    }
    // remove from global:timeline
    $r->lrem("global:timeline:", 0, $postid);
    header("Location: index.php");
?>
