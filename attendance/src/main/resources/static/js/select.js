
document.addEventListener('DOMContentLoaded', () => {
  const messages = document.querySelectorAll(
      '.message-success, .message-error, .exception-error');
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
