
function showtime(){
    let time = new Date( Date.now());
    let prettyTime = time.toLocaleString("fr-FR",{timezone : 'UTC'});

    document.getElementById("time").innerHTML = "It is  : " + prettyTime  + " and that means a JS has been loaded.";
}
