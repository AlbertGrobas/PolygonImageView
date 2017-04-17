PolygonImageView
===============

Create a custom ImageView with polygonal forms.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PolygonImageView-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1851)

![Demo Screenshot 1][1]
![Demo Screenshot 2][2]
![Demo Screenshot 3][3]

Usage
-----

To use PolygonImageView, add the module into your project and start to build xml or java.

### XML
```xml
    <net.grobas.view.PolygonImageView
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:src="@drawable/cat07"
        app:poly_shadow="true"
        app:poly_shadow_color="@android:color/black"
        app:poly_border="true"
        app:poly_border_color="@android:color/white"
        app:poly_vertices="6"
        app:poly_border_width="5dp"
        app:poly_corner_radius="5"
        app:poly_rotation_angle="25" />
```

##### Properties:

* `app:poly_vertices` (integer)       -> default 5
    * `0`  -> Circle
    * `1`  -> Regular ImageView, no affected by other properties
    * `2`  -> Square
    * `>2` -> Polygon form
* `app:poly_shadow`  (boolean)        -> default false
* `app:poly_shadow_color` (color)     -> default Black
* `app:poly_border` (boolean)         -> default false
* `app:poly_border_color` (color)     -> default White
* `app:poly_border_width` (dimension) -> default 4dp
* `app:poly_corner_radius` (float)    -> default 0.0f
* `app:poly_rotation_angle` (float)   -> default 0.0f


### JAVA

```java
    LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
    PolygonImageView view = new PolygonImageView(this);
    view.setImageResource(R.drawable.cat);
    view.addShadowResource(10f, 0f, 7.5f, R.color.shadow);
    view.addBorderResource(5, R.color.border);
    view.setCornerRadius(2);
    view.setVertices(5);
    view.setPolygonShape(new PaperPolygonShape(-15, 25));
    layout.addView(view);
```

### Effects

There are 3 basic effects:

* RegularPolygonShape
* PaperPolygonShape
* StarPolygonShape

Create your own effect overriding BasePolygonShape or interface PolygonShape.

License
-------

    Copyright 2014 Albert Grobas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



[1]: ./art/screen01.png
[2]: ./art/screen02.png
[3]: ./art/screen03.png
