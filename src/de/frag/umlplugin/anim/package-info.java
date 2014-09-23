/**
 * <style type="text/css">a:link img {border: none;vertical-align:top;} a:visited img {border: none;vertical-align:top;}</style>
 * <p>This package contains classes for animating graph elements. The y-files library (see
 * <a href="http://www.yworks.com">www.yworks.com</a>) that is used by IDEA for creating and displaying graphs
 * already includes an animation system, but it is not usable in some cases due to a bug in the y-files wrapper
 * implemented in the IDEA open API.</p>
 * <p>The classes in this package are inspired by the y-files animation system. In some aspects this implementation
 * is simpler since it is specialized for usage in this plugin.</p>
 * <p>This static class diagram shows the animation classes and their relations:
 * <a href="doc-files/animation.png" target="_blank"><img src="doc-files/animation_thumb.png" alt="UML diagram"/></a>.</p>
 * <p>The central concept for animation is the {@link de.frag.umlplugin.anim.AnimationObject AnimationObject} interface
 * that is used for every single animated object. There are several implementations of this interface for
 * specifying zoom animations, fade-in/fade-out of nodes and edges, morphs of nodes and edges and some other types.</p>
 * <p>For animating several nodes or edges simultaneously, composite animations can be created. There are two main
 * implementations of the {@link de.frag.umlplugin.anim.CompositeAnimationObject CompositeAnimationObject} interface:
 * Concurrency and Sequence. By adding atomic edge and or node animations to such composite animations and combining
 * several composite animations by using other composite animation objects, complete animation sequences containing
 * hundreds of single steps can be created.</p> 
 */
package de.frag.umlplugin.anim;