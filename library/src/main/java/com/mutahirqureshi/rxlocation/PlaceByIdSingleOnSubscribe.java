package com.mutahirqureshi.rxlocation;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.concurrent.TimeUnit;

import io.reactivex.SingleEmitter;

/* Copyright 2017 Muhammad Qureshi
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
class PlaceByIdSingleOnSubscribe extends RxLocationSingleOnSubscribe<PlaceBuffer> {

    final String[] placeIds;

    PlaceByIdSingleOnSubscribe(RxLocation rxLocation, String[] placeIds, Long timeout, TimeUnit timeUnit) {
        super(rxLocation, timeout, timeUnit);
        this.placeIds = placeIds;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, SingleEmitter<PlaceBuffer> emitter) {
        setUpPendingResult(
            Places.GeoDataApi.getPlaceById(apiClient, placeIds),
            SingleResultCallback.get(emitter)
        );
    }
}
