/* --- DROP DOWN ARROWS FOR FAQ/HELP PAGE ---------------------------------------------------------------------------------- */

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