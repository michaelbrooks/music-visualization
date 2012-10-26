# Real-Time Music Visualizations

This software contains a set of prototype real-time music visualizations
that I developed as part of my undergraduate thesis project in Computer Science
at Oberlin College, in 2010.

The purpose of the project was to design and implement an application that helps musicians 
by providing a source of real-time feedback as they practice, through (hopefully) informative
and intuitive visualizations of features such as pitch, loudness, and tone. More information 
about the project is available [here](https://sites.google.com/site/musicaudiohp).

## How to use

The project can be built with ant. There is also project information for NetBeans included,
though this is not necessary to build.

To run the compiled program:

```bash
cd dist
java -jar music-visualization.jar
```

A small screen will appear allowing you to choose from among several different visualizations.
You may also pause/resume and exit the program from this screen.

A timestamped wav file will be created in the current directory. Once the program is closed,
this will contain the audio recorded while the program was running.

## Credits

This program utilizes the excellent [JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms) 
FFT library by Piotr Wendykier.