
// ===========================
// PolicyHub Authentication JS
// ===========================

const API_BASE = "http://localhost:8082/api";


// -------------------- USER LOGIN --------------------
$("#loginForm").on("submit", async function (e) {
  e.preventDefault();
  const username = $("#loginUsername").val().trim();
  const password = $("#loginPassword").val().trim();

  if (!username || !password) {
    alert("Please fill in all fields.");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/login?username=${username}&password=${password}`, {
      method: "POST",
      credentials: "include",
    });

    if (res.ok) {
      const user = await res.json();
      if (user.role === "CUSTOMER") {
        alert("Login successful! Welcome " + user.username);
        window.location.href = "customer-dashboard.html";
      } else {
        alert("This login is for customers only. Use Admin tab for admin login.");
      }
    } else {
      alert("Invalid username or password");
    }
  } catch (err) {
    console.error("Login error:", err);
    alert("Unable to connect to server.");
  }
});

// -------------------- ADMIN LOGIN --------------------
$("#adminLoginForm").on("submit", async function (e) {
  e.preventDefault();
  const username = $("#adminUser").val().trim();
  const password = $("#adminPass").val().trim();

  if (!username || !password) {
    alert("Please fill in all fields.");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/login?username=${username}&password=${password}`, {
      method: "POST",
      credentials: "include",
    });

    if (res.ok) {
      const user = await res.json();
      if (user.role === "ADMIN") {
        alert("Welcome Admin!");
        window.location.href = "admin-dashboard.html";
      } 
      else {
        alert("You are not authorized as admin!");
      }
    } else {
      alert("Invalid admin credentials");
    }
  } catch (err) {
    console.error("Admin login error:", err);
    alert("Unable to connect to server.");
  }
});

// -------------------- USER REGISTRATION --------------------
$("#registerForm").on("submit", async function (e) {
  e.preventDefault();

  const payload = {
    fullName: $("#regName").val().trim(),
    email: $("#regEmail").val().trim(),
    phone: $("#regPhone").val().trim(),
    address: $("#regAddress").val().trim(),
    user: {
      username: $("#regUsername").val().trim(),
      password: $("#regPassword").val().trim(),
    },
  };

  // Validate form
  if (!payload.fullName || !payload.email || !payload.phone || !payload.user.username || !payload.user.password) {
    alert("Please fill all mandatory fields.");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
      credentials: "include",
    });

    if (res.ok) {
      alert("Registration successful! Please login now.");
      $("#registerForm")[0].reset();
      const tab = new bootstrap.Tab(document.querySelector("[data-bs-target='#login']"));
      tab.show();
    }   
      else {
      const data = await res.json();
      //  Handle backend validation messages
      if (typeof data === "object") {
        const messages = Object.values(data).join("<br>");
        showGlobalError(messages);
      }else {
        const data = await res.text();
        console.error("Registration failed:", data);
        showGlobalError(data);
      }
    }
  } catch (err) {
    console.error("Registration error:");

    showGlobalError(err);
    
  }
});


// -------------------- GLOBAL ERROR BOX --------------------
function showGlobalError(message) {
   // Remove old error alert
  $("#registerForm .alert-global").remove();

  const alertBox = $("<div>")
    .addClass("alert alert-danger alert-global py-2 px-3 mt-2 text-start")
    .html(message);
  $("#registerForm").prepend(alertBox);
}

// -------------------- CLEAR ERROR ON INPUT CHANGE --------------------
$("#registerForm input, #registerForm textarea").on("input", function () {
  $("#registerForm .alert-global").remove();
});




