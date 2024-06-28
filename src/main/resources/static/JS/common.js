document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('myForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('inputName').value;
        const email = document.getElementById('inputEmail').value;
        const password = document.getElementById('inputPassword').value;
        const confirmPassword = document.getElementById("inputConfirmPassword").value;

        if (name.trim() === '') {
            alert('Name is required');
            return;
        } else if (!isValidName(name)) {
            alert('Invalid Name');
            return;
        } else if (containsSQLInjection(name)) {
            alert('Possible SQL injection detected in Name');
            return;
        }

        if (email.trim() === '') {
            alert('Email is required');
            return;
        } else if (!isValidEmail(email)) {
            alert('Invalid email address');
            return;
        } else if (containsSQLInjection(email)) {
            alert('Possible SQL injection detected in Email');
            return;
        }

        if (password.trim() === '') {
            alert('Password is required');
            return;
        } else if (password.length < 6) {
            alert('Password must be at least 6 characters long');
            return;
        } else if (containsSQLInjection(password)) {
            alert('Possible SQL injection detected in Password');
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        } else if (containsSQLInjection(confirmPassword)) {
            alert('Possible SQL injection detected in Confirm Password');
            return;
        }

        this.submit();
    });

    function isValidEmail(email) {
        const emailRegex = /^[\w-\.]+@([^\d_][\w-]+\.)+[\w-]{2,4}$/;
        return emailRegex.test(email);
    }

    function isValidName(name) {
        const nameRegex = /^[a-zA-Z]+(['-]?[a-zA-Z]+)*(?:\s+[a-zA-Z]+(['-]?[a-zA-Z]+)*)*$/;
        return nameRegex.test(name);
    }

    function containsSQLInjection(input) {
        const sqlInjectionPatterns = [
            /(\b(SELECT|UPDATE|DELETE|INSERT|DROP|ALTER|TRUNCATE|EXEC)\b)/i,
            /(\b(OR|AND|--|#|;|\/\*|\*\/)\b)/i,
            /=|<|>|'|"|\(|\)/,
        ];

        return sqlInjectionPatterns.some(pattern => pattern.test(input));
    }
});