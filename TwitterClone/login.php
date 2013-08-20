<?php
    include("RDAL.php");

    // Make sure we can get the username and password
    if (!simpleGet("username") || !simpleGet("password"))
        goback("You need to enter both username and password to login.");

    $username = simpleGet("username");
    $password = simpleGet("password");
    $r = connection();

    // Make sure a userid is associated with the username - 
    // i.e. that the user exists
    $userid = $r->get("username:$username:id");
    if (!$userid)
        goBack("You have entered an incorrect user name or password.");

    // Make sure the password entered matches the one stored 
    // for that user name
    $storedPassword = $r->get("uid:$userid:password");
    if (strcmp($storedPassword, $password) != 0)
        goBack("You have entered an incorrect user name or password.");

    // All good, set a cookie logging in the user to last for one year, 
    // and redirect to index
    $auth= $r->get("uid:$userid:auth");
    setcookie("auth",$auth,time()+3600*24*365);
    header("Location: index.php");
?>
