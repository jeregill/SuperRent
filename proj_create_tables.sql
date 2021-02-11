create table VehicleType ( 
	vtname varchar(20) PRIMARY KEY,
	wrate dec(5,2)  not null,
	drate dec(5,2)  not null,
	hrate dec(4,2)  not null,
	--wirate dec(4,2) not null,
	--dirate dec(4,2) not null,
	--hirate dec(4,2) not null,
	krate dec(4,2)  not null
);

create table Status (
	status_name varchar(15) PRIMARY KEY
);

create table Customer ( 
	license_no varchar(10) PRIMARY KEY,
	first_name varchar(20) not null,
	last_name varchar(20) not null,
	phone_num varchar(14) not null,
	card_num varchar(16) not null
);

create table Branch ( 
	branch_num integer PRIMARY KEY,
	city varchar(50) not null
);

create table Vehicles (
	vlicense varchar(10) PRIMARY KEY,
	make varchar(20) not null,
	model varchar(10) not null,
	color varchar(10) not null,
	year char(4) not null,
	odometer integer not null,
	vtname varchar(20) not null,
	status varchar(15) not null,
	branch_num integer not null,
	foreign key (vtname) references VehicleType(vtname),
	foreign key (branch_num) references Branch(branch_num),
	foreign key (status) references status(status_name) 
);


create table Reservations (
	conf_num integer PRIMARY KEY,
	branch_num integer not null,
	vtname varchar(20) not null,
	cust_license_no varchar(10) not null,
	from_date date not null,
	to_date date not null,
	foreign key (cust_license_no) references customer,
	foreign key (branch_num) references branch,
	foreign key (vtname) references VehicleType
);

create table Rentals (
	rent_id integer PRIMARY KEY,
	conf_num integer not null,
	v_license varchar(10) not null, 
	from_date date not null,
	to_date date not null,
	foreign key (v_license) references Vehicles,
	foreign key (conf_num) references Reservations
);

create table Returns (
	rent_id integer PRIMARY KEY,
	return_date date not null,
	odometer integer not null, 
	full_tank integer not null,
	value dec(6,2) not null,
	foreign key (rent_id) references Rentals(rent_id)
);

commit;
