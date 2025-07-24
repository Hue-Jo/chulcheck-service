document.getElementById("scrollToBottomBtn").addEventListener("click", () => {
  window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
});

document.addEventListener('DOMContentLoaded', () => {
  const messages = document.querySelectorAll(
      '.message-success, .message-error');
  messages.forEach((msg) => {
    setTimeout(() => {
      msg.style.opacity = '0';
      msg.style.top = '0px';
      setTimeout(() => {
        msg.style.display = 'none';
      }, 1000);
    }, 2000);
  });
});

function showModal(message) {
  document.getElementById('modalErrorMessage').textContent = message;
  document.getElementById('errorModal').style.display = 'flex';
}

function closeModal() {
  window.location.href = "/";
}

const errorMessage = /*[[${chulcheckErrorMessage}]]*/ null;
if (errorMessage) {
  showModal(errorMessage);
}