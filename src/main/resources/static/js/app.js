//토스트
const toast = (msg) => {
  const t = $("#toast");
  t.text(msg).addClass("show");
  setTimeout(()=> t.removeClass("show"), 1600);
};

// 고정 확장자 로딩
function loadFixed() {
  $.get("/api/fixed", function(data) {
    $("#fixed-list").empty();
    data.forEach(function(item) {
      const chk = $("<input type='checkbox'>").prop("checked", item.checked);
      chk.change(() => {
        $.ajax({
          url: "/api/fixed",
          type: "PATCH",
          contentType: "application/json",
          data: JSON.stringify({name: item.name, checked: chk.is(":checked")}),
          success: () => toast("저장됨")
        });
      });
      $("#fixed-list").append($("<div>").append(chk).append(" " + item.name));
    });
  });
}

//커스텀 확장자 로드
function loadCustom() {
  $.get("/api/custom", function(data) {
    $("#custom-list").empty();

    $("#custom-count").text(`${data.length} / 200`);

    if (data.length > 0) {
        $("#deleteAll-custom").show();   //리스트 있으면 전체 삭제 ㅂ튼 보이기
    } else {
        $("#deleteAll-custom").hide();
    }

    data.forEach(function(item) {
      const del = $("<button>X</button>").click(() => {
        $.ajax({ url: "/api/custom/" + item.id, type: "DELETE", success: () => {
          loadCustom(); toast("삭제됨");
        }});
      });
      $("#custom-list").append($("<div>").append(item.name + " ").append(del));
    });
  });
}

function customCount(){

}

//커스텀 추가 확장자 클릭
$("#add-custom").click(() => {
  const name = $("#custom-input").val();
  if (name.length === 0) return;
  if (name.length>20){toast("20자 이상입니다");}
  $.ajax({
    url: "/api/custom",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify({name}),
    success: (msg) => {
      if (msg === "success") {
        $("#custom-input").val("");
        loadCustom();
        toast("추가됨");
      } else {
        showDialog(msg); // 중복,200개 초과일 경우,확장자 이름이 올바르지 않은 경우
        $("#custom-input").val("");
      }
    }
  });
});

//커스텀 확장자 전체 삭제
$("#deleteAll-custom").off("click").on("click", () => {
  showDialog("정말 모든 커스텀 확장자를 삭제할까요?", {
    showCancel: true,
    onOk: () => {
      $.ajax({
        url: "/api/custom/deleteAll",
        type: "DELETE",
        success: () => {
          loadCustom();
          toast("전체 삭제 완료");
        },
        error: (xhr) => {
          showDialog("삭제 실패: " + xhr.status);
        }
      });
    },
    onCancel: () => toast("취소됨")
  });
});



// 업로드
$("#upload-btn").click(() => {
  const file = $("#file-input")[0].files[0];
  if (!file) return showDialog("파일을 선택하세요.");
  const formData = new FormData();
  formData.append("file", file);
  $.ajax({
    url: "/api/upload",
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    success: () => {
      $("#upload-result").text("업로드 성공").css("color", "var(--ok)");
    },
    error: () => {
      $("#upload-result").text("차단된 확장자").css("color", "var(--danger)");
    }
  });
});

//알림창
function showDialog(msg, options = {}) {
  $("#dialog-msg").text(msg);
  const $btns = $("#dialog-buttons").empty();

  // 확인 버튼
  $("<button>")
    .addClass("ok")
    .text(options.okText || "확인")
    .click(() => {
      $("#dialog-box").addClass("hidden");
      if (options.onOk) options.onOk();
    })
    .appendTo($btns);

  // 취소 버튼 (confirm 용)
  if (options.showCancel) {
    $("<button>")
      .addClass("cancel")
      .text(options.cancelText || "취소")
      .click(() => {
        $("#dialog-box").addClass("hidden");
        if (options.onCancel) options.onCancel();
      })
      .appendTo($btns);
  }

  $("#dialog-box").removeClass("hidden");
}


// 초기 로딩시 고정확장자, 커스텀 확장자 로드
$(function() {
  loadFixed();
  loadCustom();
});
