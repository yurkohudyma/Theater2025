window.onload = function () {
  fetch('/auth/check'/*, {
    credentials: 'same-origin'
  }*/)
    .then(response => {
      if (response.status === 401) {
        showModal();
      }
    });
};

const modal = document.getElementById("authModal");
const closeBtn = document.querySelector(".close-button");

if (modal && closeBtn) {
  function showModal() {
    modal.style.display = "block";
  }

  closeBtn.addEventListener("click", () => {
    modal.style.display = "none";
  });

  window.addEventListener("click", (event) => {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  });
} else {
  console.error('Modal or Close Button not found');
}

function goToBuy() {
  fetch('/access/buy', {
    credentials: 'same-origin'
  }).then(response => {
    if (response.status === 200) {
      window.location.href = '/buy';
    } else if (response.status === 401 || response.status === 403) {
      showModal();
    }
  });
}

function showGhost(msg) {
    const ghost = document.getElementById('ghost');
    ghost.textContent = msg;
    ghost.style.opacity = '1';
    setTimeout(() => ghost.style.opacity = '0', 3000);
    console.info(msg);
}