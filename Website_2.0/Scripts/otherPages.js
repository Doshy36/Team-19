/* --- SLIDESHOW --- */

const slideshowImages = document.querySelectorAll(".intro-slideshow img");

const nextImageDelay = 5000;
let currentImageCounter = 0; // setting a variable to keep track of the current image (slide)

var greyBubbles = ["../Assets/circleG1.png", "../Assets/circleG2.png", "../Assets/circleG3.png", "../Assets/circleG4.png"];
var blueBubbles = ["../Assets/circleC1.png", "../Assets/circleC2.png", "../Assets/circleC3.png", "../Assets/circleC4.png"];
var bubbles = document.getElementsByClassName("bubble");
var bubbleCounter = 0;

// slideshowImages[currentImageCounter].style.display = "block";
slideshowImages[currentImageCounter].style.opacity = 1;

setInterval(nextImage, nextImageDelay);

function nextImage() {
  // slideshowImages[currentImageCounter].style.display = "none";
  slideshowImages[currentImageCounter].style.opacity = 0;

  currentImageCounter = (currentImageCounter+1) % slideshowImages.length;

  // slideshowImages[currentImageCounter].style.display = "block";
  slideshowImages[currentImageCounter].style.opacity = 0.95;

  bubbles[bubbleCounter].setAttribute("src", greyBubbles[bubbleCounter]);
  bubbleCounter = (bubbleCounter+1) % bubbles.length;
  bubbles[bubbleCounter].setAttribute("src", blueBubbles[bubbleCounter]);
}








/* --- SLIDESHOW DOWN ARROW CHANGING --- */

var down_arrow = document.getElementById("slideshow-down-arrow-img");

down_arrow.addEventListener('mouseover', function(){
  down_arrow.src = "../Assets/SlideshowImages/arrow_down_blue.png";
})

down_arrow.addEventListener('mouseout', function(){
    down_arrow.src = "../Assets/SlideshowImages/arrow_down.png";
})

down_arrow.onclick = function (e){
  e.preventDefault();
  document.getElementsByTagName("body")[0].style.overflow = "auto";
}




