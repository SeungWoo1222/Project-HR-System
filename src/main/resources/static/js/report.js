function toggleRejectionReason() {
    var status = document.getElementById("status").value;
    var rejectionReasonContainer = document.getElementById("rejectionReasonContainer");
    if (status === "거절") {
        rejectionReasonContainer.style.display = "block";
    } else {
        rejectionReasonContainer.style.display = "none";
    }
}

    function toggleEmployeeNameField() {
    const specificOption = document.getElementById('specificOption');
    const employeeNameField = document.getElementById('employeeName');
    employeeNameField.disabled = !specificOption.checked;
}