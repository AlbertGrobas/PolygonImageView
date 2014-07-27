PolygonImageView
===============

Create a custom ImageView with polygonal forms.


![Demo Screenshot 1][1] ![Demo Screenshot 2][2]


Usage
-----

To use PolygonImageView, add the module into your project and start to build xml or java.

###XML
```xml
    <net.grobas.view.PolygonImageView
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:src="@drawable/cat07"
        app:poly_shadow="true"
        app:poly_border="true"
        app:poly_border_color="@android:color/white"
        app:poly_vertices="6"
        app:poly_border_width="5dp"
        app:poly_corner_radius="5"
        app:poly_rotation_angle="25" />
```

#####Properties:

* `app:poly_vertices` (integer)       -> default 5
    * `0`  -> Circle
    * `1`  -> Regular ImageView, no affected by other properties
    * `2`  -> Square
    * `>2` -> Polygon form
* `app:poly_shadow`  (boolean)        -> default false
* `app:poly_border` (boolean)         -> default false
* `app:poly_border_color` (color)     -> default White
* `app:poly_border_width` (dimension) -> default 4dp
* `app:poly_corner_radius` (float)    -> default 0
* `app:poly_rotation_angle` (float)   -> default 0.0f


###JAVA

```java
    LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
    PolygonImageView view = new PolygonImageView(this);
    view.setImageResource(R.drawable.cat01);
    view.addShadow(7.5f, 0f, 7.5f, Color.RED);
    view.setBorder(true);
    view.setBorderWidth(5);
    view.setCornerRadius(5);
    view.setBorderColorResource(android.R.color.white);
    view.setVertices(6);
    layout.addView(view);
```


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

