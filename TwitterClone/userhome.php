<?php
    include("RDAL.php");
    if (!isLoggedIn()) {
        header("Location: index.php");
        exit;
    }
    include("header.php");
    $r = connection();
?>
<div id="postform">
    <form method="POST" action="post.php">
        <?php echo $User['username']; ?>, what you are doing?
        <br>
        <table>
            <tr><td><textarea cols="70" rows="3" name="status"></textarea></td></tr>
            <tr><td align="right"><input type="submit" name="doit" value="Update"></td></tr>
        </table>
    </form>
    <div id="homeinfobox">
        <!-- Output the number of followers and those we're following -->
        <?php 
            $followers = $r->scard("uid:".$User['id'].":followers");
            echo $followers;
        ?> followers<br>
        <?php 
            $following = $r->scard("uid:".$User['id'].":following");
            echo $following;
        ?> following<br>
    </div>
</div>
<?php
    $start = simpleGet("start") === false ? 0 : intval(simpleGet("start"));
    showUserPostsWithPagination(false,$User['id'],$start,10, true);
    include("footer.php");
?>
