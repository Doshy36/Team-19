/* --- SMOOTH SCROLLING --- */

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