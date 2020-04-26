/* --- SMOOTH SCROLLING ---------------------------------------------------------------------------------------- */

var anchorSelector = 'a[href^="#"]'; 
      
var anchorList = document.querySelectorAll(anchorSelector); 

anchorList.forEach(link => { 
    link.onclick = function (e) { 
        e.preventDefault(); 

        // CLOSES THE HAMBURGER MENU
   		var x = document.getElementById("myLinks");
   		x.style.display = "none";

        var destination = document.querySelector(this.hash);   
        destination.scrollIntoView({ 
            behavior: 'smooth' 
        }); 


    }
}); 

/* --- DROP DOWN ARROWS FOR FAQ ---------------------------------------------------------------------------------- */

var q = document.getElementsByClassName("question");

for (var i = 0; i < q.length; i++) {
	q[i].addEventListener("click", function() {
	    this.classList.toggle("active");
	    var answer = this.nextElementSibling;

	   	// IF ANSWER IS ALREADY OPEN... CLOSE IT AND CHANGE ARROW DIRECTION
	    if (answer.style.maxHeight) {
	    	answer.style.maxHeight = null;
	    	this.children[1].name = "chevron-down-outline";

	    } else { //IF ANSWER IS CLOSED... OPEN IT AND CHANGE ARROW DIRECTION
	    	answer.style.maxHeight = answer.scrollHeight + "px";
	    	this.children[1].name ="chevron-up-outline";
	    } 
  	});
}

/* --- HAMBURGER NAV BAR ---------------------------------------------------------------------------------- */

function hamburgerNavbar() {
  var x = document.getElementById("myLinks");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}