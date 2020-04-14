var slideshow = document.getElementById("slideshow");

const nextImageDelay = 3000;
let currentImageCounter = 0;

var images = ["url('./Assets/Alnwick_Castle.jpg')", "url('./Assets/HadriansWall.jpg')"];


setInterval(nextImage, nextImageDelay);

function nextImage() {
  currentImageCounter = (currentImageCounter+1) % images.length;
  slideshow.style.backgroundImage = images[currentImageCounter];
}



/* --- SLIDESHOW DOWN ARROW CHANGING --- */

var down_arrow = document.getElementById("slideshow-down-arrow-img");

down_arrow.addEventListener('mouseover', function(){
  down_arrow.src = "Assets/arrow_down_blue.png";
})

down_arrow.addEventListener('mouseout', function(){
    down_arrow.src = "Assets/arrow_down.png";
})

/* --- SMOOTH SCROLLING --- */

var anchorSelector = 'a[href^="#"]'; 
      
var anchorList = document.querySelectorAll(anchorSelector); 

anchorList.forEach(link => { 
    link.onclick = function (e) { 
        e.preventDefault(); 
   
        var destination = document.querySelector(this.hash); 
  
        destination.scrollIntoView({ 
            behavior: 'smooth' 
        }); 
    }
}); 