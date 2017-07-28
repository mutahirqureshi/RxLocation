package com.mutahirqureshi.rxlocationsample;

import android.location.Address;
import android.location.Location;
import android.text.style.CharacterStyle;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mutahirqureshi.rxlocation.FusedLocation;
import com.mutahirqureshi.rxlocation.GeoData;
import com.mutahirqureshi.rxlocation.Geocoding;
import com.mutahirqureshi.rxlocation.LocationSettings;
import com.mutahirqureshi.rxlocation.RxLocation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;


import static org.mockito.Mockito.*;

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
@RunWith(JUnit4.class)
public class MainPresenterTest {

    @Rule public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

    @Mock RxLocation rxLocation;
    @Mock FusedLocation fusedLocation;
    @Mock LocationSettings locationSettings;
    @Mock Geocoding geocoding;
    @Mock GeoData geoData;

    @Mock
    MainView mainView;

    MainPresenter mainPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(fusedLocation).when(rxLocation).location();
        doReturn(locationSettings).when(rxLocation).settings();
        doReturn(geocoding).when(rxLocation).geocoding();
        doReturn(geoData).when(rxLocation).geoData();

        mainPresenter = new MainPresenter(rxLocation);
    }

    @Test
    public void settingsSatisfied_1Location() {
        Location loc = Mockito.mock(Location.class);
        Address address = Mockito.mock(Address.class);

        doReturn(Single.just(true)).when(locationSettings).checkAndHandleResolution(Matchers.any(LocationRequest.class));
        doReturn(Observable.just(loc)).when(fusedLocation).updates(Matchers.any(LocationRequest.class));
        doReturn(Maybe.just(address)).when(geocoding).fromLocation(Matchers.any(android.location.Location.class));

        mainPresenter.attachView(mainView);

        verify(mainView).onLocationUpdate(loc);
        verify(mainView).onAddressUpdate(address);
        verifyNoMoreInteractions(mainView);
    }

    @Test
    public void settingsNotSatisfied_LastLocation() {
        Location loc = Mockito.mock(Location.class);
        Address address = Mockito.mock(Address.class);

        doReturn(Single.just(false)).when(locationSettings).checkAndHandleResolution(Matchers.any(LocationRequest.class));
        doReturn(Maybe.just(loc)).when(fusedLocation).lastLocation();
        doReturn(Maybe.just(address)).when(geocoding).fromLocation(Matchers.any(android.location.Location.class));

        mainPresenter.attachView(mainView);

        verify(mainView).onLocationSettingsUnsuccessful();
        verify(mainView).onLocationUpdate(loc);
        verify(mainView).onAddressUpdate(address);
        verifyNoMoreInteractions(mainView);
    }

    @Test
    public void settingsNotSatisfied_NoLastLocation() {
        doReturn(Single.just(false)).when(locationSettings).checkAndHandleResolution(Matchers.any(LocationRequest.class));
        doReturn(Maybe.empty()).when(fusedLocation).lastLocation();
        mainPresenter.attachView(mainView);

        verify(mainView).onLocationSettingsUnsuccessful();
        verifyNoMoreInteractions(mainView);
    }

    @Test
    public void onAutocompleteQueryChanged_1Prediction() {
        MainPresenter presenter = spy(mainPresenter);
        AutocompletePrediction prediction = mock(AutocompletePrediction.class);
        AutocompletePredictionBuffer buffer = mock(AutocompletePredictionBuffer.class);
        String fullText = "123 Some Address";

        doReturn(Single.just(false)).when(locationSettings).checkAndHandleResolution(any(LocationRequest.class));
        doReturn(fullText).when(prediction).getFullText(Matchers.any(CharacterStyle.class));
        doReturn(Single.just(buffer)).when(geoData).autocompletePredictions(anyString(), any(LatLngBounds.class), any(AutocompleteFilter.class));
        doReturn(Collections.singletonList(prediction).iterator()).when(buffer).iterator();
        doNothing().when(presenter).startLocationRefresh();

        presenter.attachView(mainView);
        presenter.onAutocompleteQueryChanged("some query");

        verify(mainView).onAutocompleteResultsUpdate(Collections.singletonList(fullText));
        verifyNoMoreInteractions(mainView);
    }
}
