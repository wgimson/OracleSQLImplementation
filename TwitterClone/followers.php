<?php
    include("RDAL.php");
    include("header.php");
?>
<h2>Your Followers</h2>
<?php
    $userid = $User['id'];
    showFollowers($userid);
?>
<h2></em>Latest messages from your followers</em></h2>
<?php
    $userid = $User['id'];
    echo "<div id=\"allPostsDiv\">";
    showFollowerPosts($userid, 0, 100);
    echo "</div>";
    include("footer.php");
?>
