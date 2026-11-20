/* Connects the public temple page to the Spring Boot backend.
   - Sends the enquiry form to POST /api/enquiries
   - Loads rituals and festivals from the database (falls back to the built-in HTML if the API is unreachable) */
(function () {
  "use strict";

  function el(tag, cls, text) {
    var e = document.createElement(tag);
    if (cls) e.className = cls;
    if (text != null) e.textContent = text;
    return e;
  }

  /* ---- Enquiry form (capture phase so it runs before the old demo handler) ---- */
  document.addEventListener("submit", function (event) {
    var form = event.target;
    if (!form || form.id !== "contactForm") return;
    event.preventDefault();
    event.stopImmediatePropagation();

    var msg = document.getElementById("formMessage");
    var btn = form.querySelector("button[type=submit]");
    if (!form.checkValidity()) { form.reportValidity(); return; }

    var data = {};
    new FormData(form).forEach(function (v, k) { data[k] = String(v).trim(); });
    if (btn) btn.disabled = true;
    msg.textContent = "Sending...";

    fetch("/api/enquiries", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    })
      .then(function (r) { return r.json().then(function (j) { return { ok: r.ok, body: j }; }); })
      .then(function (res) {
        msg.textContent = res.ok ? res.body.message : (res.body.error || "Could not send your enquiry.");
        if (res.ok) form.reset();
      })
      .catch(function () { msg.textContent = "Network error. Please try again."; })
      .then(function () { if (btn) btn.disabled = false; });
  }, true);

  /* ---- Rituals table ---- */
  var ACCESS = {
    PUBLIC: ["status-public", "Public Allowed"],
    CLERGY_ONLY: ["status-restricted", "Clergy Only"],
    CLOSED: ["status-closed", "Closed"]
  };

  function loadRituals() {
    var tbody = document.querySelector(".rituals-table tbody");
    if (!tbody) return;
    fetch("/api/rituals").then(function (r) { return r.ok ? r.json() : Promise.reject(); }).then(function (list) {
      if (!list.length) return;
      tbody.textContent = "";
      list.forEach(function (x) {
        var tr = el("tr");
        var td1 = el("td"); td1.appendChild(el("span", "time-label", x.timeLabel));
        var td2 = el("td"); td2.appendChild(el("strong", null, x.name)); td2.appendChild(el("small", null, x.description || ""));
        var a = ACCESS[x.access] || ACCESS.PUBLIC;
        var td3 = el("td"); td3.appendChild(el("span", "status " + a[0], a[1]));
        var td4 = el("td");
        if (x.access === "PUBLIC") { var l = el("a", "table-link", "Enquire"); l.href = "#contact"; td4.appendChild(l); }
        else td4.appendChild(el("span", "table-muted", x.access === "CLOSED" ? "Rest period" : "Internal"));
        [td1, td2, td3, td4].forEach(function (t) { tr.appendChild(t); });
        tbody.appendChild(tr);
      });
    }).catch(function () { /* keep static markup */ });
  }

  /* ---- Festivals grid ---- */
  function loadFestivals() {
    var grid = document.querySelector(".festival-grid");
    if (!grid) return;
    fetch("/api/festivals").then(function (r) { return r.ok ? r.json() : Promise.reject(); }).then(function (list) {
      if (!list.length) return;
      grid.textContent = "";
      list.forEach(function (f, i) {
        var card = el("article", "festival-card js-reveal is-visible");
        card.appendChild(el("span", "festival-number", String(i + 1).padStart(2, "0")));
        var icon = el("div", "festival-icon");
        var ic = document.createElement("iconify-icon");
        ic.setAttribute("icon", /^[a-z0-9-]+:[a-z0-9-]+$/i.test(f.icon || "") ? f.icon : "lucide:flower-2");
        icon.appendChild(ic);
        card.appendChild(icon);
        card.appendChild(el("h3", null, f.name));
        card.appendChild(el("p", null, f.description));
        grid.appendChild(card);
      });
    }).catch(function () { /* keep static markup */ });
  }

  document.addEventListener("DOMContentLoaded", function () { loadRituals(); loadFestivals(); });
})();
