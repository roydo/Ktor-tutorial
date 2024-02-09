<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Login</h3>
        <form action="/login" method="post">
            <p>
                <input type="text" name="username" >
            </p>
            <p>
                <input type="password" name="password" placeholder="パスワード" required>
            </p>
            <p>
                <button type="submit">Login</button>
            </p>
        </form>
    </div>
</@layout.header>