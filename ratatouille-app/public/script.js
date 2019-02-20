function toggleSignIn() {
    if (firebase.auth().currentUser) {
        firebase.auth().signOut();
    } else {
        var email = $("#email").val();
        var password = $("#password").val();

        console.log(email +"  :  "+password);

        firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
            // Handle Errors here.
            var errorCode = error.code;
            var errorMessage = error.message;

            if (errorCode === 'auth/wrong-password') {
                alert('Wrong password.');
            } else {
                alert(errorMessage);
            }
            console.log(error);
        });
    }
}

/*function handleSignUp() {
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;

    firebase.auth().createUserWithEmailAndPassword(email, password).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        if (errorCode === 'auth/weak-password') {
            alert('The password is too weak.');
        } else {
            alert(errorMessage);
        }
        console.log(error);
    });
}*/

function sendEmailVerification() {
    firebase.auth().currentUser.sendEmailVerification().then(function() {
        alert('Email Verification Sent!');
    });
}


function displayControlPad() {

    $("form button")
        .prop('disabled', true)
        .html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>  Loading...');

    setTimeout(function() {
            $("#identity-pad").fadeOut(complete = function() {
                $("#control-pad").fadeIn()
            });
        }, 3000
    );

}

function initApp() {
    firebase.auth().onAuthStateChanged(function(user) {

        //document.getElementById('quickstart-verify-email').disabled = true;

        if (user) {
            var displayName = user.displayName;
            var email = user.email;
            var emailVerified = user.emailVerified;
            var photoURL = user.photoURL;
            var isAnonymous = user.isAnonymous;
            var uid = user.uid;
            var providerData = user.providerData;

            $("#quickstart-sign-in-status").textContent = 'Signed in';
            $("#quickstart-sign-in").textContent = 'Sign out';
            $("#quickstart-account-details").textContent = JSON.stringify(user, null, '  ');

            //$("main").load("./form.html");

        } else {
            //$("#exercise-table").remove();

            $("#quickstart-sign-in-status").textContent = 'Signed out';
            $("#quickstart-sign-in").textContent = 'Sign in';
            $("#quickstart-account-details").textContent = 'null';
        }
    });

    $("#quickstart-sign-in").click(toggleSignIn);
    $("#quickstart-verify-email").click(sendEmailVerification);


    // TEST
    $("form button").click(displayControlPad);



}

window.onload = function() {
    initApp();

};




