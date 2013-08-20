<?php
    include("RDAL.php");

    // Check that form data is correct
    if (!simpleGet('username') || !simpleGet('password') || !simpleGet('checkpassword'))
        goBack("You're missing field!");
    if (strcmp(simpleGet('password'), simpleGet('checkpassword')) != 0)
        goBack("You're password fields don't match!");

    // Make sure the username chosen isn't already in use
    $username = simpleGet('username');
    $password = simpleGet('password');
    $r = connection();
    if ($r->get("username:$username:id"))
        goBack("Sorry, someone is already registered with that user name.");

    // Register the user
    $userid = $r->incr("global:nextUserId");
    $r->set("username:$username:id",$userid);
    $r->set("uid:$userid:username",$username);
    $r->set("uid:$userid:password",$password);

    // Generate and set random authentication sequence
    $auth = getRandomAuthentication();
    $r->set("uid:$userid:auth",$auth);
    $r->set("auth:$auth",$userid);

    ///////////////////////////////////////////////////////////////////////////////
    // JUST FOR DEBUGGING
    ///////////////////////////////////////////////////////////////////////////////
    /*echo "<p>username: " . $username . "</p>";
    echo "<p>password: " . $password . "</p>";
    echo "<p>userid: " . $userid . "</p>";
    echo "<p>auth: " . $auth . "</p>";*/

    // Add new user to set of all users
    $r->sadd("global:users",$userid);

    // Create a cookie to log the new user in - 
    // expires after a year
    setcookie("auth",$auth,time()+3600*24*365);
    ?>
    <?php include("header.php"); ?>
    <h2>Welcome aboard!</h2>
    Welcome, <?php echo "$username"; ?>, thanks for signing up! <a href="index.php">Write your first message!</a>.
<?php include("footer.php"); ?>
