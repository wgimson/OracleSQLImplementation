<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
    <head>
        <meta content="text/html; charset=UTF-8" http-equiv="content-type">
        <title>Project 5 - An Implementation of a Twitter Clone Using Redis</title>
        <link href="css/style.css" rel="stylesheet" type="text/css">
        <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
        <script src="http://code.jquery.com/jquery-migrate-1.1.1.min.js"></script>
        <script>
            $(document).ready(function() {
                $('#postform').hide();
                $('#postform').slideDown('slow');
                $('#allPostsDiv').hide();
                $('#allPostsDiv').slideDown(2000);
                $('#navbar').animate({right: '100px'});
                $('#fadeImg').hide();
                $('#fadeImg').fadeIn(1500);
            }); 
        </script>
    </head>
    <body>
        <div id="page">
        <div id="header">
            <a id="imgAnchor" href="/"><img id="fadeImg" style="border:none" src="images/logo10.jpg" width="192" height="85" alt="Project 5"></a>
            <?php include("navigation.php") ?>
        </div>
