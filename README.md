# Inertial-Sensors-Viewer
An Android application for displaying and saving data from inertial sensors embedded in the mobile device. <br /> <br />

Features:
- displaying as a time graph data from accelerometer, gyroscope and magnetometer
- saving data in the .csv format in the device's memory
- the ability to setting a scale and a range of axes by touch on the screen
- the ability to running in the background <br /> <br />

Technical solutions:
- used the Model-View-ViewModel architecture pattern
- used the Clean Architecture concept to devide the application into layers
- used GraphView library to display data as a graph
- used OpenCSV library to save data in .csv format
- created Intent Service to read data from sensors
- used the Observer design pattern and Java implementation to communicate ViewModel with Model
- used LiveData to communicate View with ViewModel
- used the Singleton design pattern to implement the data repository <br /> <br />

![Screenshot_1](https://user-images.githubusercontent.com/59321506/75930666-619ab400-5e73-11ea-84c3-bbe13a7916d4.png)
![Screenshot_2](https://user-images.githubusercontent.com/59321506/75930668-62334a80-5e73-11ea-92fb-8d4b973f4165.png)
 <br />  <br />
![Screenshot_2020-02-18-00-22-14-363_com dandrzas inertialsensorsviewer](https://user-images.githubusercontent.com/59321506/75930665-5fd0f080-5e73-11ea-8141-bb941bcdf61b.png)
