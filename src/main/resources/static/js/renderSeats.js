document.addEventListener("DOMContentLoaded", function () {
  const table = document.getElementById("myTable");
  const rows = window.rows;
  const seats = window.seats;
  const hall = window.hall;
  const sold = window.sold;

  const soldSet = new Set(sold.map(seat => `${seat.row}-${seat.seat}`));

  console.log("Sold seats:", sold);
  console.log("Sold set:", soldSet);

  for (let i = 0; i < rows; i++) {
    const tr = document.createElement("tr");
    tr.classList.add("seat-row");

    for (let j = 0; j < seats; j++) {
      const td = document.createElement("td");
      td.classList.add("seat-cell");

      const form = document.createElement("form");
      form.method = "post";
      form.action = `/buy/${hall}/${i + 1}/${j + 1}`;
      form.classList.add("seat-form");

      const button = document.createElement("button");
      button.type = "submit";
      button.textContent = j + 1;
      button.classList.add("seat-button");

      if (soldSet.has(`${i + 1}-${j + 1}`)) {
        button.disabled = true;
        button.classList.add("sold-seat");
      }

      form.appendChild(button);
      td.appendChild(form);
      tr.appendChild(td);
    }

    const rowLabel = document.createElement("td");
    rowLabel.textContent = `Ряд ${i + 1}`;
    rowLabel.classList.add("row-label");
    tr.appendChild(rowLabel);

    table.appendChild(tr);
  }
});
