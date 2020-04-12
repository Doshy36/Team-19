var slideshow = document.getElementById("slideshow");

const nextImageDelay = 3000;
let currentImageCounter = 0;

var images = ["url('./Assets/Alnwick_Castle.jpg')", "url('./Assets/HadriansWall.jpg')"]


setInterval(nextImage, nextImageDelay);

function nextImage() {
  currentImageCounter = (currentImageCounter+1) % images.length;
  slideshow.style.backgroundImage = images[currentImageCounter];
}