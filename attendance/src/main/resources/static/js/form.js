
function toggleReasonInput(studentId) {
  const presentRadio = document.querySelector(
      `input[name='status_${studentId}'][value='PRESENT']`
  );
  const reasonSection = document.querySelector(`#reasonSection_${studentId}`);

  if (presentRadio.checked) {
    reasonSection.style.display = 'none';
    const select = reasonSection.querySelector('select');
    const customInput = reasonSection.querySelector('input[type="text"]');
    select.value = '';
    select.required = false;
    customInput.style.display = 'none';
    customInput.value = '';
  } else {
    reasonSection.style.display = 'flex';
    const select = reasonSection.querySelector('select');
    const customInput = reasonSection.querySelector('input[type="text"]');
    select.required = true;
    toggleCustomReason(studentId);
  }
}

function toggleCustomReason(studentId) {
  const select = document.getElementById(`reasonSelect_${studentId}`);
  const customInput = document.getElementById(`customReason_${studentId}`);

  if (select.value === 'OTHER') {
    customInput.style.display = 'block';
  } else {
    customInput.style.display = 'none';
    customInput.value = '';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  // 페이지 로드 시 기존 상태에 따라 사유 입력창 토글
  document.querySelectorAll('.student-card').forEach(card => {
    const studentId = card.querySelector("input[type='radio']").name.replace('status_', '');
    toggleReasonInput(studentId);
  });

  // 텍스트 입력란에서 Enter키 자동 제출 방지, 키보드 밑으로 내려가게 설정
  document.querySelectorAll("input[type='text']").forEach(input => {
    input.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        e.preventDefault();
        input.blur();
      }
    });
  });

  // 새 친구 등록 폼 이벤트 바인딩
  const addStudentForm = document.getElementById("addStudentForm");
  if (addStudentForm) {
    addStudentForm.addEventListener("submit", function (event) {
      event.preventDefault();
      const formData = new FormData(addStudentForm);

      fetch('/students/add', {
        method: 'POST',
        body: formData
      })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          showToast("✅ 새 친구 등록 완료!");
          setTimeout(() => location.reload(), 1000); // 토스트 보이고 1초 후 새로고침
        } else {
          showToast("❌ 등록 실패: " + data.message);
        }
        closeStudentModal();
      })
      .catch(err => {
        showToast("⚠️ 오류 발생: " + err.message);
        closeStudentModal();
      });
    });

    // 새 친구 이름 입력창에서 Enter키 눌러도 폼 제출 안되도록 방지
    const nameInput = addStudentForm.querySelector("input[name='studentName']");
    if (nameInput) {
      nameInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
          event.preventDefault();
          nameInput.blur(); // 모바일 키보드 내려가게 처리
        }
      });
    }
  }
});

// 토스트 메시지 표시 함수
function showToast(message) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.style.visibility = "visible";
  toast.style.opacity = "1";

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.visibility = "hidden";
  }, 1000); // 1초 후 토스트 사라짐
}

// 모달 열기 함수
function openStudentModal() {
  document.getElementById("studentModal").style.display = "flex";
}

// 모달 닫기 함수
function closeStudentModal() {
  document.getElementById("studentModal").style.display = "none";
}