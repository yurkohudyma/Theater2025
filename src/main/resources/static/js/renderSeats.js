document.addEventListener("DOMContentLoaded", function () {
  const table = document.getElementById("myTable");
  const rows = window.rows;
  const seats = window.seats;

  for (let i = 0; i < rows; i++) {
    const tr = document.createElement("tr");

    for (let j = 0; j < seats; j++) {
      const td = document.createElement("td");
      const a = document.createElement("a");

      const seatNumber = j + 1;
      a.textContent = seatNumber;
      a.href = `/seat?row=${i + 1}&seat=${seatNumber}`;

      td.appendChild(a);
      tr.appendChild(td);
    }

    const rowLabel = document.createElement("td");
    rowLabel.textContent = `Ряд ${i + 1}`;
    rowLabel.style.fontWeight = "bold";
    rowLabel.style.backgroundColor = "#f0f0f0";
    tr.appendChild(rowLabel);

    table.appendChild(tr);
  }
});