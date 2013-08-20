<?php
    include("RDAL.php");
    include("header.php");
?>
<h2>Recent Members</h2>
<?php
    showRecentRegistered();
?>
<h2></em>Latest messages from our users</em></h2>
<?php
    echo "<div id=\"allPostsDiv\">";
    showAllPosts(-1, 0, 100, false);
    echo "</div>";
    include("footer.php");
?>
