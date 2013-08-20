<?php
    include("RDAL.php");

    // If user is not logged in, redirect them to 
    // login/register page 
    if (!isLoggedIn()) {
        include("header.php");
        include("registerorlogin.php");
        include("footer.php");
    // Otherwise, send them to home.php
    } else {
        header("Location:userhome.php");
        exit;
    }
?>
