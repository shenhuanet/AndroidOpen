# SharedElement

SDK>=21. This is not a backport of Transition Framework. This demo works above Lollipop only.

![screenshot](https://github.com/shenhuanet/AndroidOpen/blob/master/sharedelement/screenshot/img.gif)

# Usage
When launching another activity,specify the views where you want to  perform transition and pass it as a bundle. The launching activity and the launched activity should have common views to perform transition on that pair of views.

```java
 Intent intent=new Intent(mContext, DetailActivity.class);
 ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(),   Pair.create((View) cover, "cover"));
 mContext.startActivity(intent,options.toBundle());
```
Specify the attribute android:transitionName="cover" in both the view of two activities.Transition framework will look for the views with same transitionName attribute and will apply the auto transition on them.

We will specify the transition in our theme. Add this attribute to your v21 theme
```xml
<item name="android:windowSharedElementEnterTransition">@transition/shared_element</item>
```

We will define our own custom Transition instead of the default Transition.
##### res/transition/shared_element.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- --A set of transitions on two views performed together <!-- -->
<transitionSet
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:transitionOrdering="together"
    android:duration="240">

    <transitionSet   >
    <!-- --A custom transition defined in PlayTransition class <!-- -->
        <transition
            class="com.naman14.playanimations.PlayTransition"/>
        <targets>
         <!-- --We are excluding the second view from the custom transition <!-- -->
            <target android:excludeId="@id/icon" /> />
        </targets>
        </transitionSet>
<!-- --We have left this one upto transition Framework <!-- -->
    <autoTransition/>

</transitionSet>
```

[PlayTransition.java](https://github.com/shenhuanet/AndroidOpen/raw/master/sharedelement/src/main/java/com/shenhua/itemanimation/PlayTransition.java)
