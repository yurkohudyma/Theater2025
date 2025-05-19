document.addEventListener("DOMContentLoaded", function () {
  const selectedList = document.getElementById("selectedSeatsList");
  const totalPriceEl = document.getElementById("totalPrice");
  const payButton = document.getElementById("payButton");
  const resetButton = document.getElementById("resetButton");
  const table = document.getElementById("seatsTable");

  const rows = window.rows;
  const seats = window.seats;
  const hall_id = window.hallId;
  const user_id = window.userId;
  const soldArray = window.soldMapList;
  const movie_id = window.movieId;
  const selected_timeslot = window.selected_timeslot;
  const paymentData = window.paymentData;
  const paymentSignature = window.paymentSignature;
  const pricePerSeat = window.ticketPrice

  const soldSet = new Set(soldArray.map(seat => `${seat.row}-${seat.seat}`));
  const selectedSeats = [];

function updateSelectedSeatsUI() {
  const price = Number(pricePerSeat);
  const total = selectedSeats.length * price;

  const infoContainer = document.getElementById("selectionInfo");
  if (selectedSeats.length > 0) {
    infoContainer.style.display = "block";
  } else {
    infoContainer.style.display = "none";
  }
  // Оновлюємо список місць
  selectedList.innerHTML = "";
  selectedSeats.forEach(seat => {
    const li = document.createElement("li");
    li.textContent = `Ряд ${seat.row}, Місце ${seat.seat}`;
    selectedList.appendChild(li);
  });

  // Загальна сума — вище списку
  totalPriceEl.innerHTML = `Ціна за квиток: ${price} ₴<br><span style ="font-weight: bold;"</span>Загальна сума: ${total} ₴`;
}
  console.log("Sold seats:", soldArray);
  console.log("Sold set:", soldSet);
  console.log("Sel timeslot:", selected_timeslot);
  console.log("paymentData: ", paymentData);
  console.log("paymentSign: ", paymentSignature);
  console.log("selectedSeats: ", selectedSeats);
  console.log("ticketPrice: ", pricePerSeat);

 for (let i = 0; i < rows; i++) {
     const tr = document.createElement("tr");
     tr.classList.add("seat-row");

     for (let j = 0; j < seats; j++) {
       const td = document.createElement("td");
       td.classList.add("seat-cell");

       const button = document.createElement("button");
       button.type = "button";
       button.textContent = j + 1;
       button.classList.add("seat-button");

       button.dataset.row = i + 1;
       button.dataset.seat = j + 1;

       const seatKey = `${i + 1}-${j + 1}`;

       if (soldSet.has(seatKey)) {
         button.disabled = true;
         button.classList.add("sold-seat");
         button.textContent = "";
       } else {
         button.addEventListener("click", function () {
           const row = parseInt(this.dataset.row);
           const seat = parseInt(this.dataset.seat);
           const index = selectedSeats.findIndex(s => s.row === row && s.seat === seat);

           if (index > -1) {
             // Вилучити, якщо вже вибране
             selectedSeats.splice(index, 1);
             this.classList.remove("selected-seat");
           } else {
             // Додати нове місце
             selectedSeats.push({ row, seat });
             this.classList.add("selected-seat");
           }

           updateSelectedSeatsUI();
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

   // Кнопка оплати
   payButton.addEventListener("click", function () {
     fetch('/user/updateRowSeatDataBatch', {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       credentials: "include",
       body: JSON.stringify({
         seats: selectedSeats,
         timeslot: selected_timeslot,
         movieId: movie_id,
         hallId: hall_id,
         userId: user_id
       })
     })
       .then(response => response.json())
       .then(data => {
         createLiqpayForm(data.paymentData, data.signature);
       })
       .catch(err => {
         console.error("Помилка оплати:", err);
         alert("Сталася помилка під час оплати");
       });
   });

   resetButton.addEventListener("click", function () {
     // Зняти клас з кнопок
     document.querySelectorAll(".seat-button.selected-seat").forEach(button => {
       button.classList.remove("selected-seat");
     });

     // Очистити масив вибраних місць
     selectedSeats.length = 0;

     // Оновити UI
     updateSelectedSeatsUI();
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
});

function engageTimeSlot(url, data) {
        const form = document.createElement("form");
        form.method = "POST";
        form.action = url;

        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = key;
                input.value = data[key];
                form.appendChild(input);
            }
        }

        document.body.appendChild(form);
        form.submit();
    }