document.addEventListener("DOMContentLoaded", () => {
  const path = window.location.pathname;
  document.querySelectorAll("nav a").forEach(a => {
    if (a.getAttribute("href") === path) {
      a.classList.add("active-nav");
    }
  });
});