<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Login</h3>
        <form action="/login" method="post">
            <p>
                <input type="text" name="username" id="username">
            </p>
            <p>
                <input type="password" name="password" placeholder="パスワード" id="password" required>
            </p>
            <p>
                <button type="submit">Login</button>
            </p>
            <#if status.isfailed>
            <p class="error_message" style="color: red;">
                May be wrong username or password!
            </p>
            </#if>
        </form>
    </div>
    <script>
        const submitButton = document.querySelector("button");
        const errorMessage = document.querySelector(".error_message");

        const formData = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        };

        submitButton.addEventListener("click", () => {
            fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            })
            .then(response => {
            console.log(response);
                if(response.ok) {
                    return
                } else {
                    throw new Error();
                }
            })
            .catch(error => {
                //window.location.href = '/login'
                errorMessage.style.display = "block";
            });
        });
    </script>
</@layout.header>