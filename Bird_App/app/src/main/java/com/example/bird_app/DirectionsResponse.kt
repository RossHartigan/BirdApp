package com.example.bird_app

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overview_polyline: Polyline,
    val legs: List<Leg>
)

data class Leg(
    val steps: List<Step>
)

data class Step(
    val polyline: Polyline
)

data class Polyline(
    val points: String
)


