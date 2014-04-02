# Real-Time Music Visualizations

This software contains a set of prototype real-time music visualizations
that I developed as part of my undergraduate thesis project in Computer Science
at Oberlin College, in 2010.

The purpose of the project was to design and implement an application that helps musicians 
by providing a source of real-time feedback as they practice, through (hopefully) informative
and intuitive visualizations of features such as pitch, loudness, and tone. More information 
about the project is available [here](https://sites.google.com/site/musicaudiohp).

## How to use

This software requires [Java](java.com/download) to run.
Your computer also needs to have a microphone.

A binary distribution is available [here](https://github.com/michaelbrooks/music-visualization/releases) 
if you don't want to build it yourself.
Download the `music-visualization-dist.zip` file and extract the contents.
Inside the folder, run `java -jar music-visualization.jar` or
double-click on the jar file.

A small screen will appear allowing you to choose from among several different visualizations.
You may also pause/resume and exit the program from this screen.

A timestamped wav file will be created in the current directory. Once the program is closed,
this will contain the audio recorded while the program was running.

If you do want to build the project, 
you can use ant (just run `ant` in the cloned repository directory).
There is also project information for [NetBeans](https://netbeans.org/) included,
though this is optional.

To run the compiled program:

```bash
cd dist
java -jar music-visualization.jar
```

## Credits

This program utilizes the excellent [JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms) 
FFT library by Piotr Wendykier.
