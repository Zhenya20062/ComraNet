package com.euzhene.comranet.util

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.euzhene.comranet.destinations.AllChatsScreenDestination
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.destination

@OptIn(ExperimentalAnimationApi::class)
object AllChatsTransition:DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        return slideInHorizontally(animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing))
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition {
       return fadeOut()
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//object AuthTransition:DestinationStyle.Animated {
//    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
//        return
//    }
//}

@OptIn(ExperimentalAnimationApi::class)
object RegisterTransition:DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {
        return if (targetState.destination() == AllChatsScreenDestination) {
            slideOutHorizontally(animationSpec = snap(50))

        } else {
            slideOutHorizontally(animationSpec = snap(50))
        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
val defaultTransition = RootNavGraphDefaultAnimations(
    enterTransition = {
        slideInHorizontally(
            initialOffsetX = { it/2 },
            animationSpec = tween(durationMillis = 200)
        )

    },
    exitTransition = {
        slideOutHorizontally(animationSpec = tween(durationMillis = 200))
    },
)