function toggleRejectionReason() {
    var status = document.getElementById("status").value;
    var rejectionReasonContainer = document.getElementById("rejectionReasonContainer");
    if (status === "거절") {
        rejectionReasonContainer.style.display = "block";
    } else {
        rejectionReasonContainer.style.display = "none";
    }
}

function navigateToEdit(reportId) {
    window.location.href = '/report/edit?reportId=' + reportId;
}

function navigateToMain() {
    window.location.href = '/report/main';
}