document.addEventListener("DOMContentLoaded", function () {
  const paymentData = window.paymentData;
  const paymentSignature = window.paymentSignature;
  const table = document.getElementById("seatsTable");
  const rows = window.rows;
  const seats = window.seats;
  const hall_id = window.hallId;
  const soldArray = window.soldMapList;
  const movie_id = window.movieId;
  const selected_timeslot = window.selected_timeslot;
  const soldSet = new Set(soldArray.map(seat => `${seat.row}-${seat.seat}`));

  console.log("Sold seats:", soldArray);
  console.log("Sold set:", soldSet);
  console.log("Sel timeslot:", selected_timeslot);
  console.log("paymentData: ", paymentData);
  console.log("paymentSign: ", paymentSignature);

 for (let i = 0; i < rows; i++) {
   const tr = document.createElement("tr");
   tr.classList.add("seat-row");

   for (let j = 0; j < seats; j++) {
     const td = document.createElement("td");
     td.classList.add("seat-cell");

     const button = document.createElement("button");
     button.type = "button"; // зміна тут!
     button.textContent = j + 1;
     button.classList.add("seat-button");

     // Додаємо координати до атрибутів для зручності (опціонально)
     button.dataset.row = i + 1;
     button.dataset.seat = j + 1;

     // Деактивація куплених
     if (soldSet.has(`${i + 1}-${j + 1}`)) {
       button.disabled = true;
       button.classList.add("sold-seat");
       button.textContent = "";
     } else {
       // Прив’язка до функції створення LiqPay-форми
      /* button.addEventListener("click", function () {
         console.log(`Обране місце: ряд ${this.dataset.row}, місце ${this.dataset.seat}`);
         createLiqpayForm(paymentData, paymentSignature);
       });*/

       button.addEventListener("click", function () {
         const row = this.dataset.row;
         const seat = this.dataset.seat;
         console.log(`Обране місце: ряд ${row}, місце ${seat}`);

         // Надсилаємо запит до сервера
         fetch('/user/updateRowSeatData', {
           method: 'POST',
           headers: { 'Content-Type': 'application/json' },
           credentials: "include",
           body: JSON.stringify({
             row: row,
             seat: seat,
             timeslot: selected_timeslot,
             movieId: movie_id,
             hallId: hall_id
           })
         })
         .then(response => response.json())
         .then(data => {
           createLiqpayForm(data.paymentData, data.signature);
         });
       });


     }

     td.appendChild(button);
     tr.appendChild(td);
   }

   const rowLabel = document.createElement("td");
   rowLabel.textContent = `Ряд ${i + 1}`;
   rowLabel.classList.add("row-label");
   tr.appendChild(rowLabel);

   table.appendChild(tr);
 }
});

function createLiqpayForm(data, signature) {
  const form = document.createElement("form");
  form.method = "post";
  form.action = "https://www.liqpay.ua/api/3/checkout";
  form.acceptCharset = "utf-8";
  form.target = "_blank";

  const inputData = document.createElement("input");
  inputData.type = "hidden";
  inputData.name = "data";
  inputData.value = data;

  const inputSignature = document.createElement("input");
  inputSignature.type = "hidden";
  inputSignature.name = "signature";
  inputSignature.value = signature;

  form.appendChild(inputData);
  form.appendChild(inputSignature);

  document.body.appendChild(form);
  form.submit();
}

