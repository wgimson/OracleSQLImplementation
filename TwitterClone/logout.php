<?php
    include("RDAL.php");

    // If we are already logged out, simply redirect
    // us to index.php
    if (!isLoggedIn()) 
    {
        header("Location: index.php");
        exit;
    }

    // Get Redis connection
    $r = connection();

    $userId = $User['id'];
    $newAuth = getRandomAuthentication();
    $oldAuth = $r->get("uid:$userId:auth");

    // Delete the old authentication, create a new one, 
    // and redirect user to index.php
    $r->set("uid:$userId:auth", $newAuth);
    $r->set("auth:$newAuth", $userId);
    $r->del("auth:$oldAuth");

    header("Location: index.php");
?>
