# Reactive Location API Library for Android

[![Platform](http://img.shields.io/badge/platform-android-brightgreen.svg?style=flat)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/mutahirqureshi/RxLocation.svg?branch=master)](https://travis-ci.org/mutahirqureshi/RxLocation)
[![codecov](https://codecov.io/gh/mutahirqureshi/RxLocation/branch/master/graph/badge.svg)](https://codecov.io/gh/mutahirqureshi/RxLocation)
[![Download](https://jitpack.io/v/mutahirqureshi/RxLocation.svg)](https://jitpack.io/#mutahirqureshi/RxLocation)

This library wraps the Location APIs in [RxJava 2](https://github.com/ReactiveX/RxJava/tree/2.x) Observables, Singles, Maybes and Completables. No more managing GoogleApiClients! Also, the resolution of the location settings check is optionally handled by the lib.

For [RxJava 1](https://github.com/ReactiveX/RxJava/tree/1.x), please take a look at the [Android-ReactiveLocation](https://github.com/mcharmas/Android-ReactiveLocation) library by Michał Charmas.

# Usage

Create an RxLocation instance once, preferably in your Application's `onCreate()` or by using a dependency injection framework. The RxLocation class is very similar to the classes provided by the Location APIs. Instead of `LocationServices.FusedLocationApi.getLastLocation(apiClient)` you can use `rxLocation.location().lastLocation()`. Make sure to have the Location and Activity Recognition permissions from Marshmallow on, if they are needed by your API requests.

Example:

```java
// Create one instance and share it
RxLocation rxLocation = new RxLocation(context);

LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);

rxLocation.location().updates(locationRequest)
		.flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
		.subscribe(address -> {
			/* do something */
		});
```

The following APIs are wrapped by this library:

* `ActivityRecognition.ActivityRecognitionApi` via `rxLocation.activity()`
* `LocationServices.FusedLocationApi` via `rxLocation.location()`
* `LocationServices.GeofencingApi` via `rxLocation.geofencing()`
* `LocationServices.SettingsApi` via `rxLocation.settings()`
* `Geocoder` via `rxLocation.geocoding()`
* `Places.GeoDataApi` via `rxLocation.geoData()`

Checking the location settings is simplified with this library, by providing a `Single<Boolean>` via `rxLocation.settings().checkAndHandleResolution(locationRequest)`, which handles showing the resolution dialog if the location settings do not satisfy your request. It returns `true` if the settings are satisfied (optionally after showing the dialog, if a resolution is possible), and `false` otherwise. If you want to handle the `LocationSettingsResult` yourself, you can do so via `rxLocation.settings().check(locationRequest)`.

An optional global default timeout for all Location API requests made through the library can be set via `rxLocation.setDefaultTimeout(...)`. In addition, timeouts can be set when creating a new Observable by providing timeout parameters, e.g. `rxLocation.geofencing().add(geofencingRequest, pendingIntent, 15, TimeUnit.SECONDS)`. These parameters override the default timeout. When a timeout occurs, a StatusException is provided via `onError()`. Keep in mind that these timeouts only apply to calls to the Location API, e.g. when registering a location update listener. As an example, the timeout provided to `rxLocation.location().updates(locationRequest, 15, TimeUnit.Seconds)` does *not* mean that you will not receive location updates anymore after 15 seconds. Use `setExpirationDuration()` on your locationRequest for this use case.

Don't forget to dispose of your Subscriber/Observer when you are finished:

```java
Disposable disposable = rxLocation.location().updates(locationRequest).subscribe();

// Dispose of your Observer when you no longer need updates
disposable.dispose();
```

As an alternative, multiple Disposables can be collected to dipose of at once via `CompositeDisposable`:

```java
CompositeDisposable disposable = new CompositeDisposable();
disposable.add(rxLocation.location().updates(locationRequest).subscribe());

// Dispose of all collected Disposables at once, e.g. in onDestroy()
disposable.clear();
```

You can also obtain a `Flowable<GoogleApiClient>`, which connects on subscribe and disconnects on dispose via `GoogleApiClientFlowable.create(...)`.

The following Exceptions are thrown in the lib and provided via `onError()`:

* `StatusException`: When the call to the Location APIs was not successful or timed out
* `GoogleApiConnectionException`: When connecting to the GoogleApiClient was not successful.
* `GoogleApiConnectionSuspendedException`: When the GoogleApiClient connection was suspended.
* `SecurityException`: When you try to call an API without proper permissions.
* `LocationSettingsNotSatisfiedException`: When you use `rxLocation.settings().checkAndHandleResolutionCompletable(...)` and the location settings were not satisfied, even after handling the resolution.

When using the Geocoding component of RxLocation, exceptions can be thrown even after disposing, which can lead to app crashes. To prevent this, install a global `RxJavaPlugins.setErrorHandler()`, e.g. in your Application class.

# Sample

A basic sample app is available in the `sample` project.
Places API requires an API key. To use Places API, you need to create a project on Google API console
and obtain an API key using this [guide](https://developers.google.com/places/android/signup).
Obtained key should be exported as gradle property named: ```GMS_API_KEY``` for
example in ```~/.gradle/gradle.properties```.

# Setup

##### Gradle (JitPack)

In your root build.gradle:
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

In your project build.gradle:
```groovy
dependencies {
    compile 'com.github.mutahirqureshi:rxlocation:1.0.0'
}
```

# Testing

When unit testing your app's classes, RxLocation behavior can be mocked easily. See the `MainPresenterTest` in the `sample` project for an example test.

# Credits

This project has been forked from [RxLocation](https://github.com/patloew/RxLocation) library by Patrick Löwenstein, which is licensed under the Apache License, Version 2.0.

# License

	Copyright 2017 Muhammad Qureshi

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
