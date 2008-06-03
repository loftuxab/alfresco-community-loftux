// assume everyone is a community user
var alfCommunity = true;
model.alfCommunity = alfCommunity;

// are they a registered user?
var alfRegistered = (user.id != "guest");
model.alfRegistered = alfRegistered;

// are they an enterprise user?
var alfEnterprise = ((theme == "enterprise") && alfRegistered);
model.alfEnterprise = alfEnterprise;

// what is their service level?
var alfGold = false;
model.alfGold = alfGold;
var alfPlatinum = false;
model.alfPlatinum = alfPlatinum;
var alfDiamond = false;
model.alfDiamond = alfDiamond;

// are they an employee?
var alfEmployee = false;
model.alfEmployee = alfEmployee;

// are they a partner?
var alfPartner = false;
model.alfPartner = alfPartner;

// are they an admin?
var alfAdmin = false;
model.alfAdmin = alfAdmin;
