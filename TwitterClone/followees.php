<?php
    include("RDAL.php");
    include("header.php");
?>
<h2>People You're Following</h2>
<?php
    $userid = $User['id'];
    showFollowees($userid);
?>
<h2></em>Latest messages from your followers</em></h2>
<?php
    $userid = $User['id'];
    echo "<div id=\"allPostsDiv\">";
    showFolloweePosts($userid, 0, 100);
    echo "</div>";
    include("footer.php");
?>
