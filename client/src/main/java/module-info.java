module client {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires log4j;
	requires okhttp3;
	requires gson;

	opens ro.lbi.sqliggybank.client.view;
	opens ro.lbi.sqliggybank.client.view.login to javafx.fxml;
	opens ro.lbi.sqliggybank.client.view.register to javafx.fxml;
	opens ro.lbi.sqliggybank.client.view.dashboard to javafx.fxml;
}