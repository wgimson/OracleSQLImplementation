<div id="welcomebox">
    <div id="registerbox">
        <h2>Register!</h2>
        <b>Please register below!</b>
        <form method="POST" action="register.php">
            <table>
                <tr>
                  <td>Username</td><td><input type="text" name="username"></td>
                </tr>
                <tr>
                  <td>Password</td><td><input type="password" name="password"></td>
                </tr>
                <tr>
                  <td>Enter Password again</td><td><input type="password" name="checkpassword"></td>
                </tr>
                <tr>
                <td colspan="2" align="right"><input type="submit" name="register" value="Create an account"></td></tr>
            </table>
        </form>
        <h2>Already registered? Login here</h2>
        <form method="POST" action="login.php">
            <table>
                <tr>
                  <td>Username</td><td><input type="text" name="username"></td>
                </tr>
                <tr>
                  <td>Password</td><td><input type="password" name="password"></td>
                </tr>
                <tr>
                  <td colspan="2" align="right"><input type="submit" name="submit" value="Login"></td>
                </tr>
            </table>
        </form>
    </div>
</div>
