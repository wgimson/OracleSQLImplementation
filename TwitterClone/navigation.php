<div id="navbar">
    <a href="index.php">home</a>
    | <a href="posthistory.php">post history</a>
    <?php if(isLoggedIn()) {?>
    | <a href="logout.php">logout</a>
    | <a href="followers.php">followers</a>
    | <a href="followees.php">followees</a>
    <?php }?>
</div>
