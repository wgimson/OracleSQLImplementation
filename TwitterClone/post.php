<html>
    <head></head>
    <body>
        <?php
            include("RDAL.php");

            if (!isLoggedIn() || !simpleGet("status")) {
                header("Location:index.php");
                exit;
            }
            $r = connection(); 
            $postId = $r->incr("global:nextPostId"); 
            $status = str_replace("\n"," ",simpleGet("status"));
            $post = $User['id']."|".time()."|".$status;
            $r->set("post:$postId",$post);
            $followers = $r->smembers("uid:".$User['id'].":followers");
            if ($followers === false) $followers = Array();
            // Some PHP magic, this appends users id to end of followers array
            $followers[] = $User['id']; 

            foreach($followers as $fid) {
                $r->lpush("uid:$fid:posts",$postId);
            }
            # Push the post on the timeline, and trim the timeline to the
            # newest 1000 elements.
            $r->lpush("global:timeline",$postId);
            $r->ltrim("global:timeline",0,1000);

                    /*echo "Followers: " . print_r($followers);
                    echo "<br />";
                    echo "Next post ID: " . $postId;
                    echo "<br />";
                    echo "Post: " . $post;
                    echo "<br />";
                    echo "post:".$postId . "=> " . $post;
                    echo "<br />";
                    echo "global:timline" . "=> " . $postId;*/

            header("Location: index.php");
        ?>
    </body>
</html>
