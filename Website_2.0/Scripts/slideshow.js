const slideshowImages = document.querySelectorAll(".intro-slideshow img");

const nextImageDelay = 5000;
let currentImageCounter = 0; // setting a variable to keep track of the current image (slide)

var p = ["Discover for yourself the wonderful place that is Northumberland, with our simple and handy app",
 "Discover Northumberland is designed to help you get most out of your visit", "Easing the process of finding anything from famous landmarks to a nice place to have lunch",
"More then x locations are available"]

// slideshowImages[currentImageCounter].style.display = "block";
slideshowImages[currentImageCounter].style.opacity = 1;

setInterval(nextImage, nextImageDelay);

function nextImage() {
  // slideshowImages[currentImageCounter].style.display = "none";
  slideshowImages[currentImageCounter].style.opacity = 0;

  currentImageCounter = (currentImageCounter+1) % slideshowImages.length;

  // slideshowImages[currentImageCounter].style.display = "block";
  slideshowImages[currentImageCounter].style.opacity = 1;
  
  var para = document.getElementById("par");
  
  para.innerHTML = p[currentImageCounter];
  
}

