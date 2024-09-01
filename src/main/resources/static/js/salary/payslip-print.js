document.addEventListener("DOMContentLoaded", function() {
    function saveAsPDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF('p', 'mm', 'a4');
        const element = document.getElementById('payslip-container');

        html2canvas(element).then(canvas => {
            document.body.appendChild(canvas); // 캡처된 캔버스를 브라우저에 추가하여 확인
            const imgData = canvas.toDataURL('image/png');
            const imgWidth = 210; // A4 width in mm
            const pageHeight = 295; // A4 height in mm
            const imgHeight = canvas.height * imgWidth / canvas.width;
            let heightLeft = imgHeight;

            let position = 0;

            doc.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
            heightLeft -= pageHeight;

            while (heightLeft >= 0) {
                position = heightLeft - imgHeight;
                doc.addPage();
                doc.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
                heightLeft -= pageHeight;
            }

            doc.save(document.getElementById('title') + '(' + document.getElementById('employeeId') +')');
        });
    }

        function printPayslip() {
        window.print();
    }

        // saveAsPDF 함수 전역으로 선언
        window.saveAsPDF = saveAsPDF;
        window.printPayslip = printPayslip;
});

