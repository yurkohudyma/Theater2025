document.addEventListener("DOMContentLoaded", function () {
  const table = document.getElementById("myTable");
  const rows = window.rows;
  const seats = window.seats;

  for (let i = 0; i < rows; i++) {
    const tr = document.createElement("tr");

    for (let j = 0; j < seats; j++) {
      const td = document.createElement("td");

      const form = document.createElement("form");
      form.method = "post";
      form.action = `/tickets/addTicket/4/${i + 1}/${j + 1}`;

      const button = document.createElement("button");
      button.type = "submit";
      button.textContent = j + 1;

      form.appendChild(button);
      td.appendChild(form);
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
