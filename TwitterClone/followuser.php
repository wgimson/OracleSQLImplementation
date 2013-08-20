<?php
    include("RDAL.php");

    $r = connection();
    // Check that we got the uid of *the user to follow* as well as the follow bit
    // Also that the uid is actually associated with a username in the database
    if (!isLoggedIn() || !simpleGet("uid") || simpleGet("f") === false ||
        !($username = $r->get("uid:".simpleGet("uid").":username")))
    {
        header("Location:index.php");
        exit;
    }

    // Get the follow bit, 1 to follow, 0 to 'unfollow'
    $f = intval(simpleGet("f"));
    $uid = intval(simpleGet("uid"));
    // Only if the uid is not our own do we follow or unfollow
    if ($uid != $User['id'])
    {
        if ($f)
        {
            // Add us to the set of followers for the uid we chose to follow
            $r->sadd("uid:".$uid.":followers", $User['id']);
            // Add the uid of the person we chose to follow to our set of followers
            $r->sadd("uid:".$User['id'].":following", $uid);
        }
        // if follow bit is set to zero
        else 
        {
            // Remove us from the set of followers of this uid
            $r->srem("uid:".$uid.":followers", $User['id']);
            // Remove this uid from the set of people we follow
            $r->srem("uid:".$User['id'].":following", $uid);
        }
    }
    header("Location: userdetail.php?u=".urlencode($username));
?>
